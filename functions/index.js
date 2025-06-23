const functions = require("firebase-functions"); // ✅ Import Firebase Functions
const admin = require("firebase-admin"); // ✅ Import Firebase Admin SDK

admin.initializeApp(); // ✅ Initialize Firebase Admin

exports.sendPaymentReminder = functions.pubsub
  .schedule("every 24 hours") // Runs every day
  .timeZone("Asia/Kolkata") // Set to Indian time zone
  .onRun(async (context) => {
    const usersRef = admin.database().ref("users");

    try {
      const snapshot = await usersRef.once("value"); // Fetch all users
      snapshot.forEach((userSnapshot) => {
        const userData = userSnapshot.val();
        if (userData && userData.maintenanceDueDate) {
          const dueDate = new Date(userData.maintenanceDueDate);
          const today = new Date();

          if (today > dueDate) {
            sendNotification(userSnapshot.key, userData.name);
          }
        }
      });
    } catch (error) {
      console.error("Error fetching users:", error);
    }
    return null;
  });

async function sendNotification(userId, userName) {
  const message = {
    notification: {
      title: "Maintenance Payment Reminder",
      body: `Dear ${userName}, your maintenance payment is overdue!`,
    },
    topic: `user_${userId}`, // Ensure users subscribe to this topic
  };

  try {
    await admin.messaging().send(message);
    console.log(`Notification sent to ${userName}`);
  } catch (error) {
    console.error("Error sending notification:", error);
  }
}
