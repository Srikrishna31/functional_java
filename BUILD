load("//src:targets.bzl", "TARGETS")

alias(
    name = "functional_java",
    actual = "//src:functional_java",
)

test_suite(
    name = "test_functional_java",
    tests = ["//src:test_" + name for name, _, _, _ in TARGETS],
)
