import pandas as pd
import matplotlib.pyplot as plt

# Load CSV data
file_path = "class_counts.csv"
df = pd.read_csv(file_path)

# Get unique experiments
experiments = df["Experiment"].unique()

ylim = (0, 10)

# Create the figure and subplots
fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(10, 7), sharex=True)

# Plot ratio on the first subplot
for exp in experiments:
    exp_data = df[df["Experiment"] == exp]
    ratio = exp_data["Class 1"] / (exp_data["Class 0"] + 1e-6)  # Avoid division by zero
    ax1.plot(exp_data["Time Step"], ratio, linestyle="solid", alpha=0.7)

ax1.set_ylabel("Ratio Class 1 / Class 0")
ax1.set_ylim(ylim)
ax1.vlines(5000, 0, ylim[1], colors='red', label="Population 1 Becomes Highly Mutator")
ax1.legend()
ax1.grid(True)

# Plot average fitness for Class 0 and Class 1 on the second subplot
for exp in experiments[4:5]:
    exp_data = df[df["Experiment"] == exp]
    ax2.plot(exp_data["Time Step"], exp_data["Avg Fitness Class 0"], linestyle="solid", label="Average Fitness Class 0", color='#AEC6CF')
    ax2.plot(exp_data["Time Step"], exp_data["Avg Fitness Class 1"], linestyle="solid", label="Average Fitness Class 1", color='#FF6961')

ax2.set_xlabel("Time Step")
ax2.set_title("Fitness Evolution of Mutator vs Non Mutator")
ax2.set_ylabel("Fitness Score")
ax2.set_ylim(0.3, 0.9)
ax2.legend()
ax2.grid(True)

# Adjust layout
plt.suptitle("Curse of an Excessively High Mutation Rate")
plt.tight_layout()

# Save and show plot
plt.savefig("evolution_plot.png", dpi=300)
plt.show()
