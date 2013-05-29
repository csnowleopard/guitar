source LinuxX86Env.Set.sh
export ENVCFLAGS="--coverage"
export ENVCXXFLAGS="--coverage"
export ENVLINKFLAGS="--coverage"
cd sw
build && deliver
