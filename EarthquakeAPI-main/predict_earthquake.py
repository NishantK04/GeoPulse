from flask import Flask, request, jsonify
import numpy as np
import joblib  # If you're using a trained model
import os 

app = Flask(__name__)

# Load the trained model
model = joblib.load("earthquake_model.pkl")  # Update with your actual model file

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()  # Parse JSON request
        print("Received JSON:", data)  # Debugging

        # Extract input features
        magnitude = float(data['magnitude'])
        depth = float(data['depth'])
        latitude = float(data['latitude'])
        longitude = float(data['longitude'])
        time_between_shocks = float(data['time_between_shocks'])

        # Create feature array
        features = np.array([[magnitude, depth, latitude, longitude, time_between_shocks]])
        print("Features for Model:", features)  # Debugging

        # Make prediction
        prediction = model.predict(features)[0]
        print("Prediction Output:", prediction)  # Debugging

        # **Ensure the response is correctly formatted**
        return jsonify({"predictedMagnitude": float(prediction)})  # **Fix: Key is correctly named**
    
    except Exception as e:
        return jsonify({"error": str(e)}), 400  # Return error message if something fails

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))  # Use Render's port if available
    app.run(host="0.0.0.0", port=port, debug=True)
