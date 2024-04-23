
tasks.register<Exec>("npmInstall") {
    inputs.file("package.json")
    outputs.dir("node_modules")
    commandLine("npm", "install")
}

tasks.register<Exec>("generateDocumentation") {
    group = "documentation"
    dependsOn("npmInstall")
    inputs.file("antora-playbook.yml")
    inputs.dir("docs")
    outputs.dir("build/site")
    commandLine("npx", "antora", "antora-playbook.yml")
}
