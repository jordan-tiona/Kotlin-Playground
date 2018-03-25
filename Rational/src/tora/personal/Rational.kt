package tora.personal

import java.math.BigInteger

class Rational( val numerator: BigInteger, val denominator: BigInteger = BigInteger.ONE) {

    companion object {
        val ZERO = Rational(BigInteger.ZERO)
        val ONE = Rational(BigInteger.ONE)
    }

    private fun gcd(): BigInteger {
        var n = numerator
        var d = denominator

        while (d != BigInteger.ZERO) {
            var temp = d
            d = n % d
            n = temp
        }

         return n
    }

    private fun simplify(): Rational {
        if (this.numerator == BigInteger.ZERO) {
            return ZERO
        }

        val gcd = this.gcd()

        var num = this.numerator / gcd
        var den = this.denominator / gcd

        if (den < BigInteger.ZERO) {
            num *= BigInteger.valueOf(-1L)
            den *= BigInteger.valueOf(-1L)
        }

        return Rational(num, den)
    }

    override fun toString(): String {
        if (denominator == BigInteger.ONE) {
            return numerator.toString()
        }
        else {
            return numerator.toString() + "/" + denominator.toString()
        }
    }

    operator fun plus(other: Rational): Rational {
        val left = this.numerator * other.denominator
        val right = other.numerator * this.denominator

        return Rational(left + right, this.denominator * other.denominator).simplify()
    }

    operator fun minus(other: Rational): Rational {
        val left = this.numerator * other.denominator
        val right = other.numerator * this.denominator

        return Rational(left - right, this.denominator * other.denominator).simplify()
    }

    operator fun times(other: Rational): Rational {
        return Rational(this.numerator * other.numerator, this.denominator * other.denominator).simplify()
    }

    operator fun div(other: Rational): Rational {
        return Rational(this.numerator * other.denominator, this.denominator * other.numerator).simplify()
    }

    operator fun unaryMinus(): Rational {
        return Rational(-this.numerator, this.denominator)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Rational) {
            if ((this.numerator == other.numerator) && (this.denominator == other.denominator))
                return true
        }

        return false
    }
}

/*
class Rational( val numerator: Long, val denominator: Long = 1L) {

    companion object {
        val ZERO = Rational(0L)
        val ONE = Rational(1L)
    }

    private fun gcd(): Long {
        var n = numerator
        var d = denominator

        while (d != 0L) {
            var temp = d
            d = n % d
            n = temp
        }

        return n
    }

    private fun simplify(): Rational {
        if (this.numerator == 0L) {
            return ZERO
        }

        val gcd = this.gcd()

        var num = this.numerator / gcd
        var den = this.denominator / gcd

        if (den < 0L) {
            num *= -1L
            den *= -1L
        }

        return Rational(num, den)
    }

    override fun toString(): String {
        if (denominator == 1L) {
            return numerator.toString()
        }
        else {
            return numerator.toString() + "/" + denominator.toString()
        }
    }

    operator fun plus(other: Rational): Rational {
        val left = this.numerator * other.denominator
        val right = other.numerator * this.denominator

        return Rational(left + right, this.denominator * other.denominator).simplify()
    }

    operator fun minus(other: Rational): Rational {
        val left = this.numerator * other.denominator
        val right = other.numerator * this.denominator

        return Rational(left - right, this.denominator * other.denominator).simplify()
    }

    operator fun times(other: Rational): Rational {
        return Rational(this.numerator * other.numerator, this.denominator * other.denominator).simplify()
    }

    operator fun div(other: Rational): Rational {
        return Rational(this.numerator * other.denominator, this.denominator * other.numerator).simplify()
    }

    operator fun unaryMinus(): Rational {
        return Rational(-this.numerator, this.denominator)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Rational) {
            if ((this.numerator == other.numerator) && (this.denominator == other.denominator))
                return true
        }

        return false
    }
}
*/