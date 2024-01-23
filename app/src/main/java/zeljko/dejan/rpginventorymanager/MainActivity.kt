package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val randomSubtitleTexts = listOf(
        "AI-magine a World Beyond Reality!",
        "Adventure Awaits in Every AI-gorithm.",
        "Explore the Vastness of AI-lands.",
        "AI-dventure into the Unknown!",
        "Unleash Your AI-magination.",
        "AI-scape to Your Fantasy.",
        "Crafting Legends with AI and Wit.",
        "Where Magic Meets Machine Learning.",
        "Choose Your Path: AI Hero's Journey.",
        "AI Quest of Epic Proportions!",
        "Step Into a New World of AI.",
        "Forge Your AI-dentity.",
        "Epic AI Quests and Digital Deeds.",
        "Your Story, Our AI-rtificial World.",
        "Where Dreams AI-scend.",
        "Craft Your AI-stiny.",
        "Mysteries Await Your AI-nalysis.",
        "Be the AI-ro You Were Programmed to Be.",
        "A World of Endless AI-possibilities.",
        "AI-dventures Beyond the Horizon.",
        "Enter the Realm of FantAI-sy.",
        "Challenge AI- Fate in Your World.",
        "In AI- Pursuit of Greatness.",
        "Where Every Turn is a New AI-gorithm.",
        "Your AI-journey, Your Choices.",
        "Dive into the AI-bbys of Adventure.",
        "A Universe Coded in Wonder.",
        "Start Your Epic AI-tale.",
        "The AI-dyssey of Memories.",
        "Live the AI-gend. Be the AI-th.",
        "AI-les of Valor and Glory Await.",
        "AI World Awaiting Your Signature.",
        "Heroes Wanted: AI-ply Within!",
        "Welcome to a Land of AI-nchantment.",
        "Where the AI-mprobable Becomes Reality.",
        "AI-venture Awaits. Will You Answer?",
        "The AI-dyssey is Yours.",
        "Let AI Unfold Your Fantasy.",
        "An AI-pic Awaits Its Hero.",
        "AI, Dream, Explore.",
        "AI Journey Unlike Any Other.",
        "Chart Your AI-course.",
        "D-AI-vid Kupert Approves.",
        "Your AI-venture, Your Rules.",
        "Mythical BeAI-sts and Where to Find Them.",
        "The Ultimate AI-Playground.",
        "From AI-Zero to Hero!",
        "Buckle Up for an AI-dventure.",
        "Where You're the AI-ster of Fates.",
        "Enter, for AI-ghter and Wonder Await!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.loadButton).setOnClickListener {
            val intent = Intent(this, LoadChatActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.createButton).setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        val subtitleHeader = findViewById<android.widget.TextView>(R.id.headerSubtitle)
        subtitleHeader.text = randomSubtitleTexts.random()
    }

    override fun onResume() {
        super.onResume()
        val subtitleHeader = findViewById<android.widget.TextView>(R.id.headerSubtitle)
        subtitleHeader.text = randomSubtitleTexts.random()
    }
}
