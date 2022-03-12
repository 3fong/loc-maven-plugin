File file = new File( basedir, "build.log" );

def countMain = false
def countTest = false

file.eachLine {
    if (it.contains("files size: 1 lines num: 7"))
        countMain = true;
    if (it.contains("files size: 0 lines num: 0"))
        countTest = true;
}

if(!countMain) {
    throw new RuntimeException("incorrect src/main/java count info");
}
if(!countTest) {
    throw new RuntimeException("incorrect src/test/java count info");
}