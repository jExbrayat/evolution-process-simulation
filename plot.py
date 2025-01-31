import pandas as pd
import matplotlib.pyplot as plt

# Load CSV data
file_path = "class_counts.csv"
df = pd.read_csv(file_path)

# Get unique experiments
experiments = df["Experiment"].unique()

# Create the plot
plt.figure(figsize=(10, 5))

for exp in experiments:
    exp_data = df[df["Experiment"] == exp]
    ratio = exp_data["Class 0"] / (exp_data["Class 1"] + 1e-6)  # Avoid division by zero
    plt.plot(exp_data["Time Step"], ratio, label=f"Class 0 / Class 1 - Exp {exp}", linestyle="solid", alpha=0.7, color="purple")

# Customization
plt.xlabel("Time Step")
plt.ylabel("Ratio Class 0 / Class 1")
plt.title("Evolution of Class Ratios Over Multiple Experiments")
plt.grid(True)

# Save and show plot
plt.savefig("evolution_plot.png", dpi=300)
plt.show()