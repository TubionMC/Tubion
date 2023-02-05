import cc.polyfrost.gradle.util.versionFromBuildIdAndBranch

plugins {
    kotlin("jvm") version("1.6.10") apply(false)
    id("cc.polyfrost.multi-version.root")
}

version = property("tubion_version")!!

preprocess {
    val fabric11802 = createNode("1.18.2-fabric", 11802, "yarn")
    // val fabric11900 = createNode("1.19-fabric", 11900, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11903 = createNode("1.19.3-fabric", 11903, "yarn")

    fabric11903.link(fabric11902)
    // fabric11902.link(fabric11900)
    fabric11902.link(fabric11802)
}