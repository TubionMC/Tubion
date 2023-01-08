import gg.essential.gradle.util.versionFromBuildIdAndBranch

plugins {
    kotlin("jvm") version("1.6.10") apply(false)
    id("gg.essential.multi-version.root")
}

version = versionFromBuildIdAndBranch()
if (version.toString().contains('.')) {
    version = branch()
}

preprocess {
    val fabric11802 = createNode("1.18.2-fabric", 11802, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11903 = createNode("1.19.3-fabric", 11903, "yarn")

    fabric11903.link(fabric11902)
    fabric11902.link(fabric11802)
}

fun branch(): String = project.properties["branch"]?.toString() ?: try {
    val stdout = java.io.ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
} catch (e: Throwable) {
    "unknown"
}