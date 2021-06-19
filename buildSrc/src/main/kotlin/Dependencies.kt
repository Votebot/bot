/*
 * VoteBot - A feature-rich bot to create votes on Discord guilds.
 *
 * Copyright (C) 2019-2021  Michael Rittmeister & Yannick Seeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

private const val ktorVersion = "io.ktor:ktor-bom:1.6.0"
private const val common = ":common"
private const val client = ":client"
private const val logbackConfig = ":logback-config"

fun DependencyHandler.ktorBom(): Dependency = platform(ktorVersion)

@Suppress("unused") // used to limit scope
fun KotlinDependencyHandler.ktorBom(project: Project): Dependency = project.dependencies.platform(ktorVersion)

fun DependencyHandler.common(): Dependency = project(mapOf("path" to common))
fun KotlinDependencyHandler.common() = project(common)

fun DependencyHandler.client(): Dependency = project(mapOf("path" to client))
fun KotlinDependencyHandler.client() = project(client)

fun DependencyHandler.logbackConfig(): Dependency = project(mapOf("path" to logbackConfig))
