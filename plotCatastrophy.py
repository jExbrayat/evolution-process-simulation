import pandas as pd
import matplotlib.pyplot as plt

file_path = "class_counts.csv"
df = pd.read_csv(file_path)

experiments = df["Experiment"].unique()

ylim = (0, 5)

plt.figure(figsize=(10, 5))

for exp in experiments:
    exp_data = df[df["Experiment"] == exp]
    ratio = exp_data["Class 1"] / (exp_data["Class 0"] + 1e-6)
    plt.plot(exp_data["Time Step"], ratio, linestyle="solid", alpha=0.7)

plt.xlabel("Time Step")
plt.ylabel("Ratio Class 1 / Class 0")
plt.title("Abrupt Population Shrinkage Reverses the Trend")
plt.ylim(ylim)
plt.vlines(5000, 0, ylim[1], colors='red', label="Disease Outbreak Leads to Population Decline")
plt.legend()
plt.grid(True)

plt.savefig("evolution_plot.png", dpi=300)
plt.show()