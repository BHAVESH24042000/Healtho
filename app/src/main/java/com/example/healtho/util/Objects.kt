package com.example.healtho.util

import com.example.healtho.R
import java.util.*

object Objects {

    const val WATER_EXP = 5
    const val SLEEP_EXP = 5

    const val DATASTORE = "HealthoDataStore"
    const val USER_DOES_NOT_EXIST = "User does not exist"
    const val USER_DETAILS_UPDATED = "Details updated successfully"
    const val USER_DETAILS_UPDATE_FAILED = "Failed to update details"
    const val NO_INTERNET_MESSAGE = "Looks like you don't have an active internet connection"

    const val USER_COLLECTION = "users"
    const val WATER_COLLECTION = "water"
    const val SLEEP_COLLECTION = "sleep"
    const val WORKOUT_COLLECTION = "workout"


    const val OPEN_ADD_SLEEP_DIALOG = "AddSleep"
    const val OPEN_ADD_WATER_DIALOG = "AddWater"
    const val OPEN_WATER_LIMIT_DIALOG = "SelectWaterLimit"
    const val OPEN_SLEEP_LIMIT_DIALOG = "SelectSleepLimit"
    const val OPEN_NO_INTERNET_DIALOG = "NoInternetDialog"

    enum class ERROR_TYPE {
        NO_INTERNET, UNKNOWN
    }

    enum class WATER(val quantity: Int, val image: Int) {
        ML_200(200, R.drawable.hot_cup), ML_400(400, R.drawable.mug),
        ML_600(600, R.drawable.water_glass), ML_800(800, R.drawable.mineral_water),
        ML_1000(1000, R.drawable.water_bottle)
    }

    fun getMainGreeting(progress: Float): String {
        return when {
            progress >= 100 -> "Yay !"
            progress > 50f -> "Yay !"
            progress == 50f -> "Good going !"
            else -> "It's okay !"
        }
    }

    fun getGreeting(progress: Float): String {
        return when {
            progress >= 100 -> "You're done !"
            progress > 50f -> "You're almost done !"
            progress == 50f -> "You're half way done !"
            else -> "You can do it !"
        }
    }

    fun getTodaysTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        return calendar.timeInMillis
    }

    fun getTimeOfLastWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        return calendar.timeInMillis - (7 * 24 * 60 * 60 * 1000)
    }

    fun getTodayDayNo(): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        return cal[Calendar.DAY_OF_WEEK] - 1
    }
    val waterFOTD = listOf(
        "Water makes up approximately 70% of a human’s body weight – but DON’T stop drinking water to lose weight!",
        "Approximately 80% of your brain tissue is made of water (about the same percentage of water found in a living tree – maybe is this why people hit their heads and say “knock on wood”?).",
        "The average amount of water you need per day is about 3 liters (13 cups) for men and 2.2 liters (9 cups) for women.",
        "By the time you feel thirsty, your body has lost more than 1 percent of its total water – so let’s not feel thirst. Take a break right now and have a glass of water.",
        "Refreshed? Let’s continue. Drinking water can help you lose weight by increasing your metabolism, which helps burn calories faster.",
        "Smile. Good hydration can help reduce cavities and tooth decay. Water helps produce saliva, which keeps your mouth and teeth clean.",
        "Good hydration can prevent arthritis. With plenty of water in your body, there is less friction in your joints, thus less chance of developing arthritis."
    )

    val sleepFOTD = listOf(
        "Research shows that in the days leading up to a full moon, people go to bed later and sleep less, although the reasons are unclear.",
        "Sea otters hold hands when they sleep so they don’t drift away from each other.",
        "Tiredness peaks twice a day: Around 2 a.m. and 2 p.m. for most people. That’s why you’re less alert after lunch.",
        "Have trouble waking up on Monday morning? Blame “social jet lag” from your altered weekend sleep schedule.",
        "Today, 75% of us dream in color. Before color television, just 15% of us did.",
        "Regular exercise usually improves your sleep patterns. Strenuous exercise right before bed may keep you awake.",
        "Whales and dolphins literally fall half asleep. Each side of their brain takes turns so they can come up for air."
    )

    val exercisesInfo = listOf(
        "This is a gym classic — but you’ve gotta move fast!\n"+"\n" +"Stand with feet hip-width apart. Jump feet open as you raise arms to form an X shape. Jump feet back together as you lower arms to your sides.",
        "Stand with your back to a wall. Walk feet away from the wall as you slide your back down the wall, lowering your body until hips, knees, and ankles are at 90-degree angles. Engage core to keep low back pressed against the wall.",
        "Start in high plank, wrists under shoulders, core engaged. Lower your chest to the floor, keeping legs, hips, and back in a straight line. Press into palms to push back up.",
        "Lie faceup on the floor with knees bent and arms reaching toward feet. Press low back into the floor and engage core to lift shoulder blades off the floor and slightly forward.",
        "Stand facing a chair or stool and lift right foot onto the seat. Press into heel of right foot to lift your body onto the chair, balancing on right leg. Slowly lower back down to the floor. Switch legs and repeat. Continue to alternate.",
        "Stand with feet just wider than hip width, hips stacked over knees, knees over ankles. Hinge at hips, then send hips back.\n" +"\n" + "Bend knees to lower your body. Keep chest lifted while lowering to at least 90 degrees. Rise and repeat.",
        "Sit on the edge of a chair and place hands on edge, just outside your hips. Walk feet out a few steps, slide butt off the chair, and straighten arms.\n" + "\n" +"Bend elbows and lower your body until arms are bent at about 90 degrees. Press into the chair to return to starting position.",
        "Place hands directly under shoulders. Engage core and squeeze glutes to stabilize your body. Keep neck and spine neutral. Head should be in line with back. Hold.",
        "Stand with feet hip-width apart. Engage core and use lower abs to lift and lower one knee at a time, as if running in place.\n" + "\n" +"Bring knees to same height as hips, thighs parallel to the floor, and try not to lean back. Stay on balls of feet and alternate legs as fast as possible.",
        "Stand tall. Take a big step forward with right leg and lower your body until right thigh is parallel to the floor and right shin is vertical. Don’t let right knee go past toe.\n" + "\n" + "Press into right heel to drive back up to starting position. Repeat on other side. Continue to alternate legs.",
        "Start in high plank. Lower your body toward the floor, then press back up to perform a push-up. Shift weight to left arm and rotate your body to the left side.\n" + "\n" +"Hold side plank for 1 count, keeping hips high. Return to starting position, perform a push-up, and repeat on the right side. Continue to alternate.",
        "Lie on your side with legs and feet stacked. Lift hips and prop your body up on one elbow, keeping feet stacked. Press forearm into floor to keep torso and hips in a straight line. Hold."
    )

    val workoutString : String = "The 7-minute workout packs in a full-body exercise routine in a fraction of the time.\n" +
            "\n" + "You do each exercise for 30 seconds -- long enough to get in about 15 to 20 repetitions. In between sets you rest for about 10 seconds."


    enum class DAYS(index: Int) {
        SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(7);

        companion object {
            fun getDayFromNumber(d: Int) = when (d) {
                1 -> SUNDAY
                2 -> MONDAY
                3 -> TUESDAY
                4 -> WEDNESDAY
                5 -> THURSDAY
                6 -> FRIDAY
                7 -> SATURDAY
                else -> SUNDAY
            }
        }

        fun getShortFormFromNumber() = when (this) {
            SUNDAY -> getFirstChars()
            MONDAY -> getFirstChars()
            TUESDAY -> getFirstChars()
            WEDNESDAY -> getFirstChars()
            THURSDAY -> getTwoChars()
            FRIDAY -> getFirstChars()
            SATURDAY -> getTwoChars()
            else -> getTwoChars()
        }

        fun getFullName() = this.name.lowercase().capitalize()

        private fun getFirstChars() = this.name.substring(0, 1)

        private fun getTwoChars() = this.name.substring(0, 2).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

}