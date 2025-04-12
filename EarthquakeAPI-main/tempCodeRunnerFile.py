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
        # Get JSON data from request
        data = request.get_json()
        
        # Extract values
        magnitude = float(data['magnitude'])
        depth = float(data['depth'])
        latitude = float(data['latitude'])
        longitude = float(data['longitude'])
        time_between_shocks = float(data['time_between_shocks'])

        # Prepare input for model
        features = np.array([[magnitude, depth, latitude, longitude, time_between_shocks]])

        # Make prediction
        prediction = model.predict(features)[0]

        # Return result
        return jsonify({"predicted_magnitude": round(prediction, 2)})
    
    except Exception as e:
        return jsonify({"error": str(e)})

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)
