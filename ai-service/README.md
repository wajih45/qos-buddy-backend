# QoS Buddy — Microservice IA (Isolation Forest)

Microservice **Flask** exposant un modèle de Machine Learning non supervisé
(**Isolation Forest**, Scikit-learn) via une API REST. Il communique avec le
backend Spring Boot pour détecter automatiquement les anomalies dans les
métriques (CPU, RAM, ...).

## Bibliothèques
Flask · Scikit-learn · Pandas · NumPy · Joblib

## Contenu
- `app.py` — le microservice Flask + modèle Isolation Forest
- `requirements.txt` — dépendances Python
- `Dockerfile` — conteneurisation
- `model.joblib` — modèle entraîné (généré au 1er lancement)

---

## Lancement (JOUR J)

### Option 1 — Python directement

```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

Le service démarre sur **http://localhost:5001**

### Option 2 — Docker

```bash
cd ai-service
docker build -t qos-ai-service .
docker run -p 5001:5001 qos-ai-service
```

---

## Ordre de démarrage complet

1. PostgreSQL (port 5432)
2. **Microservice IA Flask (port 5001)**  ← ce service
3. Backend Spring Boot (port 8080)
4. Frontend React/Vite (port 5174)

> Important : lancer le service IA **avant** le backend, car Spring Boot
> l'appelle à chaque nouvelle métrique.

---

## Endpoints REST

| Méthode | URL | Description |
|---------|-----|-------------|
| GET  | `/health`        | Vérifie que le service est actif |
| POST | `/predict`       | Analyse une métrique |
| POST | `/predict-batch` | Analyse une liste de métriques |
| POST | `/retrain`       | Ré-entraîne le modèle |

### Exemple `/predict`

Requête :
```json
{ "value": 92.5 }
```

Réponse :
```json
{
  "value": 92.5,
  "anomaly": true,
  "is_anomaly": true,
  "score": -0.0879,
  "severity": "HIGH"
}
```

---

## Comment ça marche (pour la soutenance)

1. Au démarrage, le modèle **Isolation Forest** est entraîné sur un jeu de
   données représentant le comportement **normal** des métriques
   (valeurs de CPU/RAM entre 10% et 70%). Le modèle est sauvegardé avec
   **Joblib** (`model.joblib`).
2. Quand Spring Boot reçoit une nouvelle métrique, il appelle
   `POST /predict` (via `RestTemplate`).
3. Le modèle calcule un **score d'anomalie** (`decision_function`) :
   plus le score est négatif, plus le comportement est anormal.
4. Si la valeur est jugée anormale, une **Anomaly** et une **Alerte** sont
   créées automatiquement dans la base PostgreSQL.

### Pourquoi Isolation Forest ?
- Algorithme **non supervisé** : pas besoin de données étiquetées.
- Il isole les points anormaux en construisant des arbres aléatoires ;
  une anomalie est isolée en peu de découpes → chemin court dans l'arbre.
- Complète la détection par **seuil** (règles QoS) : le seuil détecte le
  dépassement d'une limite fixe, l'IA détecte les comportements
  statistiquement inhabituels même sous le seuil.
