
tasks.register<Exec>("installAntora") {
    commandLine("npm", "i", "antora")
}

tasks.register<Exec>("generateDocumentation") {
    group = "documentation"
    dependsOn("installAntora")
    commandLine("npx", "antora", "antora-playbook.yml")
}
