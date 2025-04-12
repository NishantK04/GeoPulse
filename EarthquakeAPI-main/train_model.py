import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import pickle

# Load dataset
df = pd.read_csv("earthquake_data_updated.csv")

# Convert 'date_time' to datetime format
df['date_time'] = pd.to_datetime(df['date_time'])

# Sort by date_time to ensure correct time differences
df = df.sort_values(by='date_time')

# Create 'time_between_shocks' (time difference in seconds)
df['time_between_shocks'] = df['date_time'].diff().dt.total_seconds().fillna(0)

# Define features (X) and target variable (y)
X = df[['magnitude', 'depth', 'latitude', 'longitude', 'time_between_shocks']]
y = df['magnitude']  # Example: Predicting magnitude

# Split data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train model
model = LinearRegression()
model.fit(X_train, y_train)

# Save model
with open("earthquake_model.pkl", "wb") as file:
    pickle.dump(model, file)

print("✅ Model trained successfully!")
