import matplotlib.pyplot as plt
import sys
import numpy as np
from simulation import get_p

latencies = np.loadtxt(sys.argv[1])
plt.hist(latencies, bins=100)
plt.title(sys.argv[2])
plt.show()

ps = [0.5, 0.9, 0.99, 0.999, 0.9999, 0.99999]
names = ["50", "90", "99", "999", "9999", "99999"]
for p, name in zip(ps, names):
    val = get_p(latencies, p)
    print "p%s: %f" % (name, val)
print

