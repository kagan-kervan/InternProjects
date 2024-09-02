import os
import time


def find_pom_directories(root_dir):
    pom_dirs = []
    for dirpath, dirnames, filenames in os.walk(root_dir):
        if 'pom.xml' in filenames:
            pom_dirs.append(dirpath)
            # Remove subdirectories from the search to prevent going deeper
            dirnames.clear()
    return pom_dirs


def save_paths_to_file(pom_dirs, output_file):
    with open(output_file, 'w') as file:
        for path in pom_dirs:
            file.write(f"{path}\n")


if __name__ == "__main__":
    root_directory = input("Enter the root directory path: ")
    output_file = "pom_directories.txt"

    pom_directories = find_pom_directories(root_directory)
    save_paths_to_file(pom_directories, output_file)

    for n in pom_directories:
        print(n)

    print(f"Found {len(pom_directories)} directories containing pom.xml files. Paths saved to {output_file}.")
    time.sleep(3)
