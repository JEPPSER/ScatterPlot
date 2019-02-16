## Axis tick value ranges
I made an algorithm that sets axis tick values automatically. It is quite complicated and has several steps
to decide start/end values and what increment to use between each tick. I will describe the steps in detail below:
1. Find the max and min values of the entries and calculate the difference.
2. Get the significant digit of the difference (in 8192 the significant digit is 8 and in 0.0453 it is 4). Add 1 to that value.
This will be used to calculate how many ticks the axis should have. We also want to create a variable that will works as a kind of "scale"
for the iterations and set it to 1.
3. If number of ticks is below 6, multiply it by 2 and set the iteration scale variable to 0.5.
4. Now we want to calculate how much to iterate between each tick. We do this by taking the base 10 logarithm of the difference of the
max and min values. We will then floor the value. Then we will take 10 to the power of that value. Finally we will multiply the value
by our iteration scale variable. Example: diff = 356 -> log(diff) = 2.55 -> floor(2.55) = 2 -> 10^2 = 100 -> 100 * 0.5 = 50.
The resulting value for iteration is 50.
5. We will then calculate what tick value to start on. We do this by dividing the min value by the iteration value and flooring it.
Then we multiply it by the iteration value. Example: min = 123, it = 50 -> min / it = 2.46 -> floor(2.46) = 2 -> 2 * it = 100.
The resulting value for the first tick is 100.
6. Now we want to set all ticks by starting at our start value and looping through the number of ticks and iterating
by our iteration value each time.
