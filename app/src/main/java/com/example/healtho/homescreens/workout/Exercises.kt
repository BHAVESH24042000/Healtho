package com.example.healtho.homescreens.workout

import com.example.healtho.R
import com.example.healtho.homescreens.models.ExerciseModel

class Exercises {

    companion object{
        fun defaultExerciseList():ArrayList<ExerciseModel>{
            val exercisesList = ArrayList<ExerciseModel>()

            val jumpingJacks = ExerciseModel(1,
                "Jumping Jacks",
                R.drawable.jumpingjacks1,
                false,
                false)
            exercisesList.add(jumpingJacks)

            val wallSit = ExerciseModel(2,
                "Wall Sit",
                R.drawable.wallsits1,
                false,
                false)
            exercisesList.add(wallSit)

            val pushUp = ExerciseModel(3,
                "Push Ups",
                R.drawable.pushup1,
                false,
                false)
            exercisesList.add(pushUp)

            val abCrunch = ExerciseModel(4,
                "Abdominal Crunch",
                R.drawable.ab_crunches1,
                false,
                false)
            exercisesList.add(abCrunch)

            val stepUp = ExerciseModel(5,
                "Step-Up Onto Chair",
                R.drawable.stepupontochair1,
                false,
                false)
            exercisesList.add(stepUp)

            val squat = ExerciseModel(6,
                "Squat",
                R.drawable.squats1,
                false,
                false)
            exercisesList.add(squat)

            val tricepsDip = ExerciseModel(7,
                "Triceps Dip On Chair",
                R.drawable.tricepdips1,
                false,
                false)
            exercisesList.add(tricepsDip)

            val plank = ExerciseModel(8,
                "Plank",
                R.drawable.plank1,
                false,
                false)
            exercisesList.add(plank)

            val highKnees = ExerciseModel(9,
                "High Knees Running In Place",
                R.drawable.highknees1,
                false,
                false)
            exercisesList.add(highKnees)

            val lunge = ExerciseModel(10,
                "Lunges",
                R.drawable.lunges_1,
                false,
                false)
            exercisesList.add(lunge)

            val pushUpAndRotation = ExerciseModel(11,
                "Push ups And Rotation",
                R.drawable.pushuprotate1,
                false,
                false)
            exercisesList.add(pushUpAndRotation)

            val sidePlank = ExerciseModel(12,
                "Side Plank",
                R.drawable.sideplank1,
                false,
                false)
            exercisesList.add(sidePlank)

            return exercisesList
        }
    }
}