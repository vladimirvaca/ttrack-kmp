plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

// Git hooks installation tasks
tasks.register<Copy>("installGitHooks") {
    group = "git hooks"
    description = "Installs Git pre-commit hook for ktlint check"

    from(file("scripts/git-hooks/pre-commit"))
    into(file(".git/hooks"))
    filePermissions {
        user {
            read = true
            write = true
            execute = true
        }
        group {
            read = true
            execute = true
        }
        other {
            read = true
            execute = true
        }
    }

    doLast {
        println("✅ Git hooks installed successfully!")
    }
}

// Automatically install git hooks on every compile/run of the project
gradle.projectsEvaluated {
    val installGitHooks = tasks.named("installGitHooks")
    subprojects {
        tasks.matching { it.name.startsWith("compile") || it.name == "assemble" || it.name == "build" }
            .configureEach {
                dependsOn(installGitHooks)
            }
    }
}

