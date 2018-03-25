package tora.personal

import java.util.Random
import tora.personal.Rational

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

class Matrix private constructor(val width: Int, val height: Int) {
    var elements: Array<Array<Rational>> = Array(height, { _ -> Array(width, { _ -> Rational(0L)}) })

    companion object {

        fun empty(w: Int, h: Int): Matrix {
            var new = Matrix(w, h)
            return new
        }

        fun identity(d: Int): Matrix {
            var new = Matrix(d, d)
            for (row in 0 until d) {
                for (col in 0 until d) {
                    if (row == col)
                        new.elements[row][col] = Rational(1L)
                }
            }
            return new
        }

        fun of(w: Int, h: Int, vararg values: Long): Matrix {
            require(values.size <= w * h) { "Too many values to fill matrix" }
            var new = Matrix(w, h)

            for (i in values.indices) {
                val col = i / w
                val row = i % w
                new.elements[col][row] = Rational(i.toLong())
            }

            return new
        }

        fun random(w: Int, h: Int, range: ClosedRange<Int>): Matrix {
            var new = Matrix(w, h)
            for (row in 0 until h)
                for (col in 0 until w)
                    new.elements[row][col] = Rational(range.random().toLong())

            return new
        }

        private fun dot(first: Array<Rational>, second: Array<Rational>): Rational {
            require (first.size == second.size) { "Dot product requires same dimensions" }

            var zipped = first.zip(second)
            var result = Rational(0L)
            for ( pair in zipped) {
                result += pair.first * pair.second
            }

            return result
        }
    }

    operator fun plus(other: Matrix): Matrix {
        require((this.width == other.width) && (this.height == other.height)) { "Addition of matrices require same dimensions" }
        var new = Matrix(this.width, this.height)

        for (x in this.elements.indices) {
            for (y in this.elements[x].indices) {
                new.elements[x][y] = this.elements[x][y] + other.elements[x][y]
            }
        }

        return new
    }

    operator fun minus(other: Matrix): Matrix {
        require((this.width == other.width) && (this.height == other.height)) { "Subtraction of matrices require same dimensions" }
        var new = Matrix(this.width, this.height)

        for (x in this.elements.indices) {
            for (y in this.elements[x].indices) {
                new.elements[x][y] = this.elements[x][y] - other.elements[x][y]
            }
        }

        return new
    }

    operator fun unaryMinus(): Matrix {
        var new = Matrix(this.width, this.height)

        for (x in this.elements.indices) {
            for (y in this.elements[x].indices) {
                new.elements[x][y] = -this.elements[x][y]
            }
        }

        return new
    }

    operator fun times(other: Rational): Matrix {
        var new = Matrix(this.width, this.height)

        for (x in this.elements.indices) {
            for (y in this.elements[x].indices) {
                new.elements[x][y] = this.elements[x][y] * other
            }
        }

        return new
    }

    operator fun times(other: Matrix): Matrix {
        require (this.width == other.height) { "Multiplication of matrices requires height of M1 to equal width of M2" }
        var new = Matrix(this.height, other.width)
        var other = other.transpose()

        for (thisRow in this.elements.indices) {
            for (otherRow in other.elements.indices) {
                new.elements[thisRow][otherRow] = dot(this.elements[thisRow], other.elements[otherRow])
            }
        }
        return new
    }

    fun copyOf(): Matrix {
        var new = empty(width, height)
        for (row in this.elements.indices)
            new.elements[row] = this.elements[row].copyOf()

        return new
    }

    fun transpose(): Matrix {
        var new = Matrix(height, width)

        for (x in this.elements.indices) {
            for (y in this.elements[x].indices) {
                new.elements[y][x] = this.elements[x][y]
            }
        }

        return new
    }

    fun isIdentity(): Boolean {
        if (this.height != this.width) {
            return false
        }

        for (row in this.elements.indices) {
            for (col in this.elements[row].indices) {
                if (row == col) {
                    if (this.elements[row][col] != Rational(1L)) {
                        return false
                    }
                }
                else {
                    if (this.elements[row][col] != Rational(0L)) {
                        return false
                    }
                }
            }
        }

        return true
    }

    fun det(): Rational {
        if (width != height )
            throw ArithmeticException("Determinant requires square matrix")

        val ref = this.rowEchelon()

        var det = Rational(1L)
        for (i in 0 until ref.height) {
            det *= ref.elements[i][i]
        }

        return det
    }

    fun rowEchelon(): Matrix {
        var new = copyOf()

        var i = 0
        var j = 0

        first@ while (j < new.width && i < new.height) {
            //Find first non-zero below or including i,j
            var r = i
            var pivot = new.elements[r][j]

            while (pivot == Rational(0L)) {
                r++
                if (r >= new.height) {
                    //We didn't find any non-zero elements, increment j and move on
                    j++
                    continue@first
                }
                pivot = new.elements[r][j]
            }

            //Swap row r with row i
            var temp = new.elements[i]
            new.elements[i] = new.elements[r]
            new.elements[r] = temp

            //For each row r below i, subtract M[r][j]/M[i][j] * row i
            if (r >= new.height) {
                break@first
            }

            for (r in (i + 1) until new.height) {
                var k = new.elements[r][j] / new.elements[i][j]
                for (l in new.elements[r].indices) {
                    new.elements[r][l] -= k * new.elements[i][l]
                }
            }

            //Increment i and j
            i++
            j++
        }
        return new
    }

    fun reducedRowEchelon(): Matrix {
        var new = copyOf()

        var i = 0
        var j = 0

        first@ while (j < new.width && i < new.height) {
            //Find first non-zero below or including i,j
            var r = i
            var pivot = new.elements[r][j]

            while (pivot == Rational(0L)) {
                r++
                if (r >= new.height) {
                    //We didn't find any non-zero elements, increment j and move on
                    j++
                    continue@first
                }
                pivot = new.elements[r][j]
            }

            //Swap row r with row i
            var temp = new.elements[i]
            new.elements[i] = new.elements[r]
            new.elements[r] = temp

            //Divide row i by M[i][j]
            for (k in new.elements[i].indices) {
                new.elements[i][k] /= pivot
            }

            //For each row r (if it's not the last row) subtract M[r][j] * row i from that row
            if (r >= new.height) {
                break@first
            }

            for (r in 0 until new.height) {
                if (r != i) {
                    val k = new.elements[r][j]
                    for (l in new.elements[r].indices) {
                        new.elements[r][l] -= k * new.elements[i][l]
                    }
                }
            }

            //Increment i and j
            i++
            j++
        }
        return new
    }

    private fun append(other: Matrix): Matrix {
        var new = empty(this.width + other.width, this.height)

        for (row in new.elements.indices) {
            for (col in this.elements[row].indices) {
                new.elements[row][col] = this.elements[row][col]
            }
            for (col in other.elements[row].indices) {
                new.elements[row][col + this.width] = other.elements[row][col]
            }
        }

        return new
    }

    fun inverse(): Matrix {
        if (this.width != this.height) {
            throw ArithmeticException("Inverse requires square matrix")
        }

        if (this.det() == Rational(0L)) {
            throw ArithmeticException("Inverse does not exist, determinant is zero")
        }

        var new = this.copyOf()

        new = new.append(identity(this.width))

        new = new.reducedRowEchelon()

        var inverse = empty(this.width, this.height)
        for (row in inverse.elements.indices) {
            for (col in inverse.elements[row].indices) {
                inverse.elements[row][col] = new.elements[row][col + inverse.width]
            }
        }

        return inverse
    }

    fun dump() {
        for (row in elements) {
            for (col in row) {
                print(col.toString() + " \t")
            }
            println()
        }
    }

    override fun toString(): String {
        var str = ""

        for (row in elements) {
            for (col in row) {
                str += col.toString() + " \t"
            }
            str += "\n"
        }

        return str
    }
}

fun main(args: Array<String>) {
    var testMatrix = Matrix.random(6, 6, 0..10)
    testMatrix.dump()
    println()
    print(testMatrix.det())
}