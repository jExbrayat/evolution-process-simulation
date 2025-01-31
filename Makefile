compiler = javac
runner = java

src_dir = .
build_dir = build

target = EvolutionSimulation

build:
	$(compiler) $(src_dir)/$(target).java

run:
	$(runner) $(target) 0 10
	