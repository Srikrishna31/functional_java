# This file contains all the test targets that need to be built.
# The format is:
# 1. Name of the target
# 2. List of files to build including the test class (should start with test/java/com/
# 3. Package path to the Test class
# 4. Additional dependencies to be included.
TARGETS = [
    #TODO: Figure out a way to initialize dependencies variable if the list is not empty
    ("case", ["test/java/com/functional/CaseTest.java"], "com.functional.CaseTest", []),
    ("collectionutilities", ["test/java/com/util/CollectionUtilitiesTest.java"], "com.util.CollectionUtilitiesTest",
        []),
    ("executable", ["test/java/com/functional/ExecutableTest.java"], "com.functional.ExecutableTest", []),
    ("tailcall", ["test/java/com/functional/TailCallTest.java"], "com.functional.TailCallTest", []),
    ("function", ["test/java/com/functional/FunctionTest.java"], "com.functional.FunctionTest", []),
    ("memoize", ["test/java/com/util/MemoizeTest.java"], "com.util.MemoizeTest", []),
    ("list", ["test/java/com/util/ListTest.java"], "com.util.ListTest", []),
    ("option", ["test/java/com/util/OptionTest.java"], "com.util.OptionTest", []),
    ("either", ["test/java/com/util/EitherTest.java"], "com.util.EitherTest", []),
    ("result", ["test/java/com/util/ResultTest.java"], "com.util.ResultTest", []),
    ("stream", ["test/java/com/lazy/StreamTest.java"], "com.lazy.StreamTest", []),
    ("tree", ["test/java/com/util/TreeTest.java"], "com.util.TreeTest", []),
    ("rbtree", ["test/java/com/util/RBTreeTest.java"], "com.util.RBTreeTest", []),
    ("rng", ["test/java/com/state/JavaRNGTest.java"], "com.state.JavaRNGTest", []),
    ("state", ["test/java/com/state/StateTest.java"], "com.state.StateTest", [":atm_simulator"]),
]
