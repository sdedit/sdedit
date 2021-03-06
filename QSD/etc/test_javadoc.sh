cd ..

rm -rf doc

jarfile=$(realpath __dist/sdedit-jar/sdedit*jar)

javadoc \
 -tagletpath $jarfile \
 -classpath $jarfile \
 -taglet net.sf.sdedit.taglet.SequenceTaglet \
 -sourcepath src/main/java:../QSD-lib/src/main/java \
 -subpackages . \
 -encoding utf-8 \
 -docencoding utf-8 \
 -Xdoclint:none \
 -d doc
 
