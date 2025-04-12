from flask import Flask, request, jsonify
import pickle
import numpy as np

app = Flask(__name__)

# Load trained model
with open("earthquake_model.pkl", "rb") as f:
    model = pickle.load(f)

@app.route('/')
def home():
    return "Earthquake Prediction API is running!"

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        print("Received JSON:", data)  # Check JSON data

        if data is None:
            return jsonify({"error": "No JSON received"})

        # Extract values
        magnitude = float(data.get('magnitude', 0))
        depth = float(data.get('depth', 0))
        latitude = float(data.get('latitude', 0))
        longitude = float(data.get('longitude', 0))
        time_between_shocks = float(data.get('time_between_shocks', 0))

        # Prepare input for model
        features = np.array([[magnitude, depth, latitude, longitude, time_between_shocks]])
        print("Features for Model:", features)

        # Make prediction
        prediction = model.predict(features)[0]  # Check if this line fails
        print("Prediction Output:", prediction)

        return jsonify({"predicted_magnitude": round(prediction, 2)})

    except Exception as e:
        print("Error:", str(e))  # Print error in console
        return jsonify({"error": str(e)})



if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)  # Allows external access


