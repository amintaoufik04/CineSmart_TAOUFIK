require("dotenv").config();

const express = require("express");
const cors = require("cors");
const { createClient } = require("@supabase/supabase-js");
const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// --- CONFIGURATION MONGODB ---
mongoose.connect(process.env.MONGODB_URI)
  .then(() => console.log("✅ Connecté à MongoDB"))
  .catch(err => console.error("❌ Erreur de connexion MongoDB:", err));

// Modèle Utilisateur pour MongoDB
const userSchema = new mongoose.Schema({
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
});

const User = mongoose.model("User", userSchema);

// --- CONFIGURATION SUPABASE ---
const supabase = createClient(
  process.env.SUPABASE_URL,
  process.env.SUPABASE_ANON_KEY
);

// Route test
app.get("/", (req, res) => {
  res.json({ message: "Serveur backend OK (Mongo + Supabase)" });
});

// --- AUTHENTIFICATION AVEC MONGODB ---

// Inscription
app.post("/auth/signup", async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ error: "Email et mot de passe obligatoires" });
    }

    // Vérifier si l'utilisateur existe déjà
    const userExists = await User.findOne({ email });

    if (userExists) {
      return res.status(400).json({ error: "Cet utilisateur existe déjà" });
    }

    // Hasher le mot de passe
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = new User({
      email,
      password: hashedPassword,
    });

    const savedUser = await newUser.save();

    // Générer un token JWT
    const token = jwt.sign(
      { userId: savedUser._id },
      process.env.JWT_SECRET || "votre_secret",
      { expiresIn: "7d" }
    );

    return res.status(201).json({
      message: "Inscription réussie sur MongoDB",
      userId: savedUser._id,
      token: token,
    });

  } catch (err) {
    console.error(err);
    return res.status(500).json({
      error: "Erreur serveur lors de l'inscription",
    });
  }
});

// Connexion
app.post("/auth/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ error: "Email et mot de passe obligatoires" });
    }

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(401).json({ error: "Identifiants invalides" });
    }

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(401).json({ error: "Identifiants invalides" });
    }

    const token = jwt.sign(
      { userId: user._id },
      process.env.JWT_SECRET || "votre_secret",
      { expiresIn: "7d" }
    );

    return res.status(200).json({
      message: "Connexion réussie",
      userId: user._id,
      token: token,
    });

  } catch (err) {
    console.error(err);
    return res.status(500).json({
      error: "Erreur serveur",
    });
  }
});

// --- FAVORIS AVEC SUPABASE ---

// Récupérer les favoris
app.get("/favorites/:userId", async (req, res) => {
  const { userId } = req.params;

  try {
    const { data, error } = await supabase
      .from("favorites")
      .select("*")
      .eq("user_id", userId);

    if (error) {
      return res.status(400).json({ error: error.message });
    }

    return res.status(200).json(data);

  } catch (err) {
    console.error(err);
    return res.status(500).json({
      error: "Erreur interne du serveur",
    });
  }
});

// Ajouter un favori
app.post("/favorites/add", async (req, res) => {
  const {
    userId,
    movieId,
    title,
    posterPath,
    overview,
    voteAverage,
  } = req.body;

  if (!userId || !movieId) {
    return res.status(400).json({
      error: "Données manquantes",
    });
  }

  try {
    const { data, error } = await supabase
      .from("favorites")
      .insert([
        {
          user_id: userId,
          movie_id: movieId,
          title: title,
          poster_path: posterPath,
          overview: overview,
          vote_average: voteAverage,
        },
      ])
      .select();

    if (error) {
      return res.status(400).json({
        error: error.message,
      });
    }

    return res.status(200).json({
      message: "Film ajouté aux favoris !",
      data: data,
    });

  } catch (err) {
    console.error(err);
    return res.status(500).json({
      error: "Erreur serveur",
    });
  }
});

// Supprimer un favori
app.delete("/favorites/:userId/:movieId", async (req, res) => {
  const { userId, movieId } = req.params;

  try {
    const { data, error } = await supabase
      .from("favorites")
      .delete()
      .eq("user_id", userId)
      .eq("movie_id", movieId)
      .select();

    if (error) {
      return res.status(400).json({
        error: error.message,
      });
    }

    return res.status(200).json({
      message: "Film retiré des favoris",
      data: data,
    });

  } catch (err) {
    console.error(err);
    return res.status(500).json({
      error: "Erreur serveur",
    });
  }
});

// Lancer le serveur
app.listen(PORT, () => {
  console.log(`🚀 Serveur lancé sur http://localhost:${PORT}`);
});