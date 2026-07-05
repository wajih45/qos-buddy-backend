"""
QoS Buddy - Microservice IA (Détection d'anomalies)
---------------------------------------------------
Microservice Flask exposant un modèle Machine Learning non supervisé
(Isolation Forest) via une API REST.

Le microservice communique avec l'application Spring Boot :
  - Spring Boot envoie les métriques (CPU, RAM ...) à ce service
  - Le service entraîne / applique le modèle Isolation Forest
  - Il retourne pour chaque métrique : is_anomaly, score, severity

Bibliothèques : Flask, Scikit-learn, Pandas, NumPy, Joblib
"""

import os
import numpy as np
import pandas as pd
from flask import Flask, request, jsonify
from sklearn.ensemble import IsolationForest
import joblib

app = Flask(__name__)

MODEL_PATH = "model.joblib"

# ---------------------------------------------------------------------------
# 1. Entraînement du modèle Isolation Forest
# ---------------------------------------------------------------------------
# En pratique, le modèle apprend le comportement "normal" des métriques.
# Ici, on génère un jeu de données de référence (valeurs normales de CPU/RAM
# entre 10% et 70%) pour entraîner le modèle au premier démarrage.
# ---------------------------------------------------------------------------

def train_model():
    """Entraîne un Isolation Forest sur des données normales de référence."""
    rng = np.random.RandomState(42)

    # Comportement normal : CPU/RAM majoritairement entre 10 et 70 %
    normal_data = rng.uniform(low=10, high=70, size=(500, 1))

    df = pd.DataFrame(normal_data, columns=["value"])

    model = IsolationForest(
        n_estimators=100,      # nombre d'arbres
        contamination=0.05,    # proportion attendue d'anomalies
        random_state=42
    )
    model.fit(df[["value"]])

    joblib.dump(model, MODEL_PATH)
    print(">> Modèle Isolation Forest entraîné et sauvegardé :", MODEL_PATH)
    return model


def load_or_train_model():
    """Charge le modèle sauvegardé, sinon l'entraîne."""
    if os.path.exists(MODEL_PATH):
        print(">> Chargement du modèle existant :", MODEL_PATH)
        return joblib.load(MODEL_PATH)
    return train_model()


model = load_or_train_model()


# ---------------------------------------------------------------------------
# 2. Fonction d'analyse d'une métrique
# ---------------------------------------------------------------------------

def analyze_value(value: float):
    """
    Applique le modèle Isolation Forest à une valeur de métrique.
    Retourne un dictionnaire : is_anomaly, score, severity.
    """
    df = pd.DataFrame([[value]], columns=["value"])

    # prediction : 1 = normal, -1 = anomalie
    prediction = int(model.predict(df[["value"]])[0])

    # score de décision : plus il est négatif, plus c'est anormal
    raw_score = float(model.decision_function(df[["value"]])[0])

    is_anomaly = prediction == -1

    # Calcul de la sévérité à partir du score et de la valeur
    if is_anomaly:
        if value >= 85:
            severity = "HIGH"
        elif value >= 70:
            severity = "MEDIUM"
        else:
            severity = "LOW"
    else:
        severity = "NORMAL"

    return {
        "value": value,
        # "anomaly" et "is_anomaly" ont la même valeur :
        # "anomaly" est la clé attendue par le backend Spring Boot,
        # "is_anomaly" est conservée pour la lisibilité.
        "anomaly": is_anomaly,
        "is_anomaly": is_anomaly,
        "score": round(raw_score, 4),
        "severity": severity
    }


# ---------------------------------------------------------------------------
# 3. Endpoints REST
# ---------------------------------------------------------------------------

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP", "model": "Isolation Forest"})


@app.route("/predict", methods=["POST"])
def predict():
    """
    Analyse une seule métrique.
    Corps attendu (JSON) : { "value": 92.5 }
    """
    data = request.get_json(force=True)
    value = float(data.get("value"))
    result = analyze_value(value)
    return jsonify(result)


@app.route("/predict-batch", methods=["POST"])
def predict_batch():
    """
    Analyse une liste de métriques.
    Corps attendu (JSON) : { "values": [23.4, 91.2, 55.0] }
    """
    data = request.get_json(force=True)
    values = data.get("values", [])
    results = [analyze_value(float(v)) for v in values]
    return jsonify({"results": results})


@app.route("/retrain", methods=["POST"])
def retrain():
    """Ré-entraîne le modèle (endpoint utilitaire)."""
    global model
    model = train_model()
    return jsonify({"status": "retrained", "model": "Isolation Forest"})


# ---------------------------------------------------------------------------
# 4. Lancement du microservice
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    # Port 5001 pour ne pas entrer en conflit avec le frontend / backend
    app.run(host="0.0.0.0", port=5001, debug=True)
