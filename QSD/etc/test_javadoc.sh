cd ..

rm -rf doc

cd src/main/java

find . -type f -name "*.java" | xargs \
javadoc \
 -tagletpath __dist/sdedit-jar/sdedit-4.2-SNAPSHOT.jar \
 -classpath __dist/sdedit-jar/sdedit-4.2-SNAPSHOT.jar \
 -taglet net.sf.sdedit.taglet.SequenceTaglet \
 -sourcepath . \
 -encoding utf-8 \
 -docencoding utf-8 \
 -d ../../../doc \
