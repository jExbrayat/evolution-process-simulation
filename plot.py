import pandas as pd
import matplotlib.pyplot as plt

# Charger les données CSV
file_path = "class_counts.csv"
df = pd.read_csv(file_path)

# Création du graphique
plt.figure(figsize=(10, 5))
plt.plot(df["Time Step"], df["Class 0"], label="Class 0 (Bleu)", color="blue")
plt.plot(df["Time Step"], df["Class 1"], label="Class 1 (Jaune)", color="gold")

# Personnalisation
plt.xlabel("Time Step")
plt.ylabel("Nombre d'individus")
plt.title("Évolution des Classes dans la Simulation")
plt.legend()
plt.grid(True)

# Sauvegarde et affichage
plt.savefig("evolution_plot.png", dpi=300)
plt.show()
