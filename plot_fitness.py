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
    plt.plot(exp_data["Time Step"], exp_data["Avg Fitness Class 0"], linestyle="solid", alpha=0.7, color="blue", label="Avg Fitness Class 0" if exp == experiments[0] else "")
    plt.plot(exp_data["Time Step"], exp_data["Avg Fitness Class 1"], linestyle="solid", alpha=0.7, color="gold", label="Avg Fitness Class 1" if exp == experiments[0] else "")

# Customization
plt.xlabel("Time Step")
plt.ylabel("Average Fitness")
plt.title("Evolution of Average Fitness Over Multiple Experiments")
plt.legend()
plt.grid(True, color="gray")

# Save and show plot
plt.savefig("average_fitness_plot.png", dpi=300)
plt.show()
