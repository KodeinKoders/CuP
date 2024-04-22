
tasks.register<Exec>("installAntora") {
    commandLine("npm", "install")
}

tasks.register<Exec>("generateDocumentation") {
    group = "documentation"
    dependsOn("installAntora")
    commandLine("npx", "antora", "antora-playbook.yml")
}
