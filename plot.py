import pandas as pd
import matplotlib.pyplot as plt


file_path = "class_counts.csv"
df = pd.read_csv(file_path)
experiments = df["Experiment"].unique()
plt.figure(figsize=(10, 5))

for exp in experiments:
    exp_data = df[df["Experiment"] == exp]
    plt.plot(exp_data["Time Step"], exp_data["Class 0"], linestyle="solid", alpha=0.7, color="blue")
    plt.plot(exp_data["Time Step"], exp_data["Class 1"], linestyle="solid", alpha=0.7, color="gold")


plt.xlabel("Time Step")
plt.ylabel("Number of Individuals")
plt.title("Evolution of Classes Over Multiple Experiments")
plt.legend(["Type 0", "Type 1"], title="Legend")
plt.grid(True, color="gray")


plt.savefig("evolution_plot.png", dpi=300)
plt.show()