import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Define file paths and titles
file_paths = ["data/popsize040.csv", "data/popsize030.csv", "data/popsize020.csv", "data/popsize010.csv"]
titles = ["40 - 60 %", "30 - 70 %", "20 - 80 %", "10 - 90 %"]

# Create subplots
fig, axes = plt.subplots(nrows=2, ncols=2, figsize=(10, 7))

# Set global y-axis limit
global_ylim = (0, 6)

# Plot data in subplots
for ax, file_path, title in zip(axes.flatten(), file_paths, titles):
    # Load CSV data
    df = pd.read_csv(file_path)

    # Get unique experiments
    experiments = df["Experiment"].unique()

    # Plot each experiment
    for exp in experiments:
        exp_data = df[df["Experiment"] == exp]
        ratio = exp_data["Class 1"] / (exp_data["Class 0"] + 1e-6)  # Avoid division by zero
        # Take the log
        ratio = np.log(ratio)
        ax.plot(exp_data["Time Step"], ratio, linestyle="solid", alpha=0.7)

    # Customization for each subplot
    ax.set_xlabel("Time Step")
    ax.set_ylabel(r"$log(\frac{\text{# individuals 1}}{\text{# individuals 0}})$")
    ax.set_title(title)
    ax.set_ylim(global_ylim)
    ax.set_xlim(0, 12500)
    ax.vlines(5000, global_ylim[0], global_ylim[1], colors='red', label="Individuals 0 become mutator")
    ax.legend()
    ax.grid(True)

# Adjust layout and display
plt.suptitle("Mutator Allele, Initialy Dominated, Taking Over its Adversary Population")
plt.tight_layout()
plt.savefig("evolution_plots.png", dpi=300)
plt.show()
