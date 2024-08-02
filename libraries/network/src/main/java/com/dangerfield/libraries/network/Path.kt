package com.dangerfield.libraries.network

import oddoneout.core.Catching
import oddoneout.core.logOnFailure

/**
 * For the time being this initiative is being postponed. The goal with Path is to
 * allow for an interface of a document storage system that allows for querying with paths
 * and lets firebase be abstracted
 */
class Path internal constructor(){

    val steps: MutableList<PathSegment> = mutableListOf()

    companion object {
        fun fromString(path: String): Path {
            val builder = Builder().apply {
                val segments = path.split("&")
                segments.forEach { segment ->
                    val split = segment.split("=")
                    when(split[0]) {
                        "collection" -> collection(split[1])
                        "document" -> document(split[1])
                        "field" -> field(split[1])
                    }
                }
            }

            return builder.build()
        }
    }

    class Builder {
        internal val path = Path()
        private val collectionBuilder = CollectionBuilder()
        private val documentBuilder = DocumentBuilder()
        private val fieldBuilder = FieldBuilder()

        fun collection(collection: String): CollectionBuilder {
            path.steps.add(PathSegment.Collection(collection))
            return collectionBuilder
        }

        internal fun document(document: String): Builder {
            path.steps.add(PathSegment.Document(document))
            return this
        }

        internal fun field(field: String): Builder {
            path.steps.add(PathSegment.Field(field))
            return this
        }

        internal fun build(): Path {
            Catching {
                path.validate()
            }
                .logOnFailure()

            return path
        }

        inner class DocumentBuilder internal constructor() {

            fun collection(collection: String): CollectionBuilder {
                path.steps.add(PathSegment.Collection(collection))
                return collectionBuilder
            }


            fun field(field: String): FieldBuilder {
                path.steps.add(PathSegment.Field(field))
                return fieldBuilder
            }

            fun build(): Path {
                Catching {
                    path.validate()
                }
                    .logOnFailure()

                return path
            }
        }

        inner class CollectionBuilder internal constructor() {

            fun document(document: String): DocumentBuilder {
                path.steps.add(PathSegment.Document(document))
                return documentBuilder
            }

            fun build(): Path {
                Catching {
                    path.validate()
                }
                    .logOnFailure()

                return path
            }
        }

        inner class FieldBuilder internal constructor() {

            fun field(field: String): FieldBuilder {
                path.steps.add(PathSegment.Field(field))
                return fieldBuilder
            }

            fun build(): Path {
                Catching {
                    path.validate()
                }
                    .logOnFailure()

                return path
            }
        }
    }

    override fun toString(): String {
        return steps.joinToString("&") {
            when(it) {
                is PathSegment.Collection -> "collection=${it.name}"
                is PathSegment.Document -> "document=${it.id}"
                is PathSegment.Field -> "field=${it.name}"
            }
        }
    }

    /**
     * Validates the path to ensure it follows the rules
     * @throws IllegalArgumentException if the path is invalid
     * @param path the path to validate
     *
     * rules:
     * cannot call collection twice in a row
     * cannot call document twice in a row
     * cannot call field unless its following a document or another field
     * cannot call collection unless its first or following a document
     */
    @Suppress("ThrowsCount")
    internal fun validate() {
        steps.forEachIndexed { index, segment ->
            if(index == 0) {
                if(segment !is PathSegment.Collection) {
                    throw IllegalArgumentException("First segment must be a collection. path=$this")
                }
            } else {
                val previousSegment = steps[index - 1]
                when(segment) {
                    is PathSegment.Collection -> {
                        if(previousSegment is PathSegment.Collection) {
                            throw IllegalArgumentException("Cannot call collection twice in a row. path=$this")
                        }
                    }
                    is PathSegment.Document -> {
                        if(previousSegment is PathSegment.Document) {
                            throw IllegalArgumentException("Cannot call document twice in a row. path=$this")
                        }
                    }
                    is PathSegment.Field -> {
                        if(previousSegment !is PathSegment.Document && previousSegment !is PathSegment.Field) {
                            throw IllegalArgumentException("Cannot call field unless its following a document or another field. path=$this")
                        }
                    }
                }
            }
        }
    }
}

sealed class PathSegment {
    data class Collection(val name: String): PathSegment()
    data class Document(val id: String): PathSegment()
    data class Field(val name: String): PathSegment()
}