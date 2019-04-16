import yaml


def load_bodies():
    """ Base Information https://naif.jpl.nasa.gov/pub/naif/toolkit_docs/FORTRAN/req/naif_ids.html"""
    with open(r'bodies.yaml') as f:
        return yaml.safe_load(f)


def save_bodies(bodies):
    with open(r'bodies_out.yaml', "w") as f:
        yaml.dump(bodies, f)
