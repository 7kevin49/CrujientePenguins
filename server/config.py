import yaml

with open("apiconfig.yml", "r") as yml_file:
    config = yaml.safe_load(yml_file)
