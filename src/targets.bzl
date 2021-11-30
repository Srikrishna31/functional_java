# This file contains all the test targets that need to be built.
# The format is:
# 1. Name of the target
# 2. Path to the test class (should start with test/java/com/
# 3. Package path to the Test class
TARGETS = [
    ("case", "test/java/com/functional/CaseTest.java", "com.functional.CaseTest"),
    ("list", "test/java/com/util/ListTest.java", "com.util.ListTest"),
    ("executable", "test/java/com/functional/ExecutableTest.java", "com.functional.ExecutableTest"),
    ("tailcall", "test/java/com/functional/TailCallTest.java", "com.functional.TailCallTest"),
    ("function", "test/java/com/functional/FunctionTest.java", "com.functional.FunctionTest"),
]
