import yaml


def load_bodies(file):
    """ Base Information https://naif.jpl.nasa.gov/pub/naif/toolkit_docs/FORTRAN/req/naif_ids.html"""
    new_bodies = []
    with open(file) as f:
        data = yaml.safe_load(f)
        for body in data['bodies']:
            if body not in new_bodies:
                new_bodies.append(body)
            else:
                print('Removing redundant body: %s' % (body))
        data['bodies'] = new_bodies
        return data


def save_bodies(file, bodies):
    with open(file, "w") as f:
        yaml.dump(bodies, f)
