Imports System

Module VbHiPrime
    Private _maxKeep As Integer = 80000
    Private _prime As Long() = New Long(_maxKeep+70000) {}
    Private  _offSet As Long
    Private MaxInd As Long
    Private MaxPrime As Long
    Sub PrimeByEuler(page As Long)
        Dim num = New Boolean(page + 1 - 1) {}
        For i = 2 To page
            If Not num(i) Then
                MaxPrime = i
                _prime(MaxInd - _offSet) = MaxPrime
                MaxInd += 1
            End If
            Dim j = 0
            While j < MaxInd AndAlso CLng(i)*_prime(j) <= page
                num(i*_prime(j)) = True
                If i Mod _prime(j) = 0 Then Exit While
                j += 1
            End While
        Next
    End Sub

    Sub PrimeByEratosthenes(pos As Long, page As Long)
        Dim num = New Boolean(page - 1) {}
        Dim i = 0
        While _prime(i) < Math.Sqrt(pos + page)
            Dim p As Long = _prime(i)
            Dim j As Long = CLng((Math.Ceiling(CDbl(pos)/p)*p))

            While j < pos + CLng(page)
                num(CInt((j - pos))) = True
                j += p
            End While
            i += 1
        End While

        For i = 0 To num.Length - 1
            If Not num(i) Then
                MaxPrime = pos + i
                _prime(MaxInd - _offSet) = MaxPrime
                MaxInd += 1
            End If
        Next
    End Sub

    Sub Main(args As String())
        Console.WriteLine("Hi Prime! I'm Visual Basic.Net :-)")
        Dim limit As Long = Int64.Parse(args(0))
        Dim page As Long = Int64.Parse(args(1))

        Console.WriteLine("Calculate prime numbers up to {0} using partitioned Eratosthenic sieve", limit)
        Dim start_time = DateTime.Now
        PrimeByEuler(page)
        For i = 1 To limit/page - 1
            PrimeByEratosthenes(page*i, page)
            If (MaxInd > _maxKeep)  Then _offSet = MaxInd-_maxKeep
        Next

        Dim totalTime = CLng((DateTime.Now.Subtract(start_time).TotalMilliseconds))
        Console.WriteLine("VB.net finished within {0:0.#e+00} the {1}th prime is {2}, cost time:{3}ms",
                          limit, MaxInd, MaxPrime, totalTime)
    End Sub
End Module
