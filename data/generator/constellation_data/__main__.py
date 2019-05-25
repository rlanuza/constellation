__author__ = "Roberto Lanuza"
__date__ = "14-Apr-2019"
__version__ = "1.1.0"
__email__ = 'rolf2000@gmail.com'

from . import *
import argparse

parser = argparse.ArgumentParser(prog='Constellation_data',
                                 description='Import Data for Rlanuza Constellation simulator')
parser.add_argument('-b', '--bodies', default='bodies.yaml', required=True,
                    help=r'Input yaml with body list')
parser.add_argument('-y', '--yaml_out',
                    help='Output body list in yaml format')
parser.add_argument('-c', '--config_txt', required=True,
                    help=r'Input yaml with body list')
parser.add_argument('-o', '--output', default='bodies.txt', required=True,
                    help=r'Output in txt format')

args = parser.parse_args()

bodies = load_bodies(args.bodies)

update_bodies(bodies)

save_bodies(args.yaml_out, bodies)

export_config(args.config_txt, args.output, bodies)
