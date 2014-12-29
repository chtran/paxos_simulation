import matplotlib.pyplot as plt
import numpy as np
import sys
import pdb

EDGES_DIR = '/Users/chtran/research/kraska/paxos_simulation/edges/'
SERVERS = {
        'e1': 'east-1',
        'e2': 'east-1',
        'm': 'west-1',
        'w1': 'west-1',
        'w2': 'west-2',
        'home': 'west-2'}
ps = [0.5, 0.9, 0.99, 0.999, 0.9999, 0.99999]
names = ["50", "90", "99", "999", "9999", "99999"]

class Server(object):
    def __init__(self, name, x, y):
        self.name = name
        self.position = np.array((x, y))


def _get_gaussian_delays(from_server, to_server, N, std=5.0):
    distance = np.linalg.norm(from_server.position - to_server.position)
    delays = distance + std*np.random.randn(1, N)
    return np.squeeze(delays)

def _get_exponential_delays(from_server, to_server, N, ld=1.0):
    distance = np.linalg.norm(from_server.position - to_server.position)
    delays = distance * np.random.exponential(ld, N)
    return np.squeeze(delays)

def _get_sample_delays(from_server, to_server, N):
    from_name = SERVERS[from_server.name]
    to_name = SERVERS[to_server.name]

    delays_file = '%s_%s.txt' % (from_name, to_name)
    delays = np.loadtxt(EDGES_DIR + delays_file)
    return np.random.choice(delays, N, replace=True)

def _get_closest_servers(leader, servers, K):
    distances = []
    for server in servers:
        d = np.linalg.norm(leader.position - server.position)
        distances.append((server, d))
    sorted_dist = sorted(distances, key=lambda tup: tup[1])
    #pdb.set_trace()
    return [tup[0] for tup in sorted_dist[:K]]


def get_delays_classic(user, leader, servers, N, get_delays=_get_gaussian_delays):
    print "Simulating Classic Paxos"
    to_leader = get_delays(user, leader, N) # 1XN
    num_servers = float(len(servers))
    matrix = np.zeros((num_servers, N))
    num_required = np.ceil(num_servers/2)
    print "Num required:", num_required

    for i, server in enumerate(servers):
        matrix[i, :] = get_delays(leader, server, N)
    matrix = np.sort(matrix, axis=0)
    #pdb.set_trace()


    to_return = matrix[num_required-1,:] + to_leader
    for p, name in zip(ps, names):
        val = get_p(to_return, p)
        print "p%s: %f" % (name, val)
    return to_return


def get_delays_fast(user, leader, servers, N, get_delays=_get_gaussian_delays):
    print "Simulating Fast Paxos"
    num_servers = float(len(servers))
    matrix = np.zeros((num_servers, N))
    num_required = num_servers - np.floor(num_servers/4)
    print "Num required:", num_required

    for i, server in enumerate(servers):
        matrix[i, :] = get_delays(leader, server, N)
    matrix = np.sort(matrix, axis=0)
    to_return = matrix[num_required-1,:]
    for p, name in zip(ps, names):
        val = get_p(to_return, p)
        print "p%s: %f" % (name, val)
    return matrix[num_required-1,:]


def get_delays_fixed(user, leader, servers, N, get_delays=_get_gaussian_delays):
    print "Simulating Fixed Paxos"
    num_servers = float(len(servers))
    matrix = np.zeros((num_servers, N))
    num_required = int(np.ceil(num_servers/2))
    closest_servers = _get_closest_servers(leader, servers, num_required)
    print "Num required:", num_required

    for i, server in enumerate(closest_servers):
        matrix[i, :] = get_delays(user, server, N)
    matrix = np.sort(matrix, axis=0)
    #pdb.set_trace()
    to_return = matrix[-1,:]
    for p, name in zip(ps, names):
        val = get_p(to_return, p)
        print "p%s: %f" % (name, val)
    return to_return

def get_p(array, p):
    N = len(array)
    i = p*N
    sorted_array = np.sort(array)
    return sorted_array[i]

def plot_locations(servers, user, leader):
    x = [s.position[0] for s in servers if s.name != leader.name]
    y = [s.position[1] for s in servers if s.name != leader.name]
    plt.scatter(x, y, s=100, c='red', marker = 'o')
    plt.scatter(leader.position[0], leader.position[1], s=100, c='green', marker='o')
    plt.scatter(user.position[0], user.position[1], s=100, c='blue', marker='o')

    plt.show()

if __name__ == "__main__":
    N = 1000000
    user = Server('home', -100, 110)
    e1 = Server('e1', 100, 100)
    e2 = Server('e2', 100, 120)
    m = Server('m', -100, 140)
    w1 = Server('w1', -100, 150)
    w2 = Server('w2', -100, 100)
    servers = [e1,e2,m,w1,w2]
    leader = m

    if sys.argv[1] == "gaussian":
        get_delays = _get_gaussian_delays
    elif sys.argv[1] == "exponential":
        get_delays = _get_exponential_delays
    elif sys.argv[1] == "sample":
        get_delays = _get_sample_delays

    plot_locations(servers, user, leader)
    classic = get_delays_classic(user, leader, servers, N, get_delays=get_delays)
    plt.hist(classic, bins=100)
    plt.title('Clasic Paxos')
    plt.xlim(0, 2000)
    plt.show()

    fast = get_delays_fast(user, leader, servers, N, get_delays=get_delays)
    plt.hist(fast, bins=100)
    plt.title('Fast Paxos')
    plt.xlim(0, 2000)
    plt.show()


    fixed = get_delays_fixed(user, leader, servers, N, get_delays=get_delays)
    plt.hist(fast, bins=100)
    plt.title('Fixed Paxos')
    plt.xlim(0, 2000)
    plt.show()
