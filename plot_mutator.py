import pandas as pd
import matplotlib.pyplot as plt

file_path = "class_counts.csv"
df = pd.read_csv(file_path)

experiments = df["Experiment"].unique()

plt.figure(figsize=(10, 5))

for exp in experiments:
    exp_data = df[df["Experiment"] == exp]
    ratio = exp_data["Class 1"] / (exp_data["Class 0"] + 1e-6)
    plt.plot(exp_data["Time Step"], ratio, linestyle="solid", alpha=0.7)

plt.xlabel("Time Step")
plt.ylabel("Ratio Class 1 / Class 0")
plt.title("Evolution of the Population in Presence of a Mutator Allele")
plt.ylim(0, 3)
plt.xlim(0, 12500)
plt.vlines(5000, 0, 3, colors='red', label="Individuals 0 become mutator")
plt.legend()
plt.grid(True)

plt.text(1000, 2.7, r"$\mu_0 = \mu_1 = 10^{-5}$", 
         fontsize=12, bbox=dict(facecolor='none', edgecolor='black', boxstyle='square,pad=0.2'))

plt.text(10000, 2.3, r"$\mu_0 = 10^{-3}$" "\n" r"$\mu_1 = 10^{-5}$", 
         fontsize=12, bbox=dict(facecolor='none', edgecolor='black', boxstyle='square,pad=0.2'))

plt.savefig("evolution_plot.png", dpi=300)
plt.show()