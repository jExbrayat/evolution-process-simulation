import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

file_paths = ["data/popsize040.csv", "data/popsize030.csv", "data/popsize020.csv", "data/popsize010.csv"]
titles = ["40 - 60 %", "30 - 70 %", "20 - 80 %", "10 - 90 %"]

fig, axes = plt.subplots(nrows=2, ncols=2, figsize=(10, 7))

global_ylim = (0, 6)

for ax, file_path, title in zip(axes.flatten(), file_paths, titles):
    df = pd.read_csv(file_path)

    experiments = df["Experiment"].unique()

    for exp in experiments:
        exp_data = df[df["Experiment"] == exp]
        ratio = exp_data["Class 1"] / (exp_data["Class 0"] + 1e-6)
        ratio = np.log(ratio)
        ax.plot(exp_data["Time Step"], ratio, linestyle="solid", alpha=0.7)

    ax.set_xlabel("Time Step")
    ax.set_ylabel(r"$log(\frac{\text{# individuals 1}}{\text{# individuals 0}})$")
    ax.set_title(title)
    ax.set_ylim(global_ylim)
    ax.set_xlim(0, 12500)
    ax.vlines(5000, global_ylim[0], global_ylim[1], colors='red', label="Individuals 0 become mutator")
    ax.legend()
    ax.grid(True)

plt.suptitle("Mutator Allele, Initialy Dominated, Taking Over its Adversary Population")
plt.tight_layout()
plt.savefig("evolution_plots.png", dpi=300)
plt.show()
