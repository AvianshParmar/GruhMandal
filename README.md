<h1 align="center">🏘️ Society Care – Android Application</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform" />
  <img src="https://img.shields.io/badge/Database-Firebase-orange.svg" alt="Database" />
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" />
  <img src="https://img.shields.io/badge/Language-Java-yellow.svg" alt="Language" />
</p>

---

## 📱 Overview

**Society Care** is a modern, user-friendly Android app designed to streamline society/apartment management. It enables residents to manage bills, complaints, announcements, and facility bookings — all in one place.

---

## ✨ Features

### 👤 For Residents:
- 🔔 View society **announcements**.
- 💳 Pay **maintenance bills** securely.
- 📜 Download **invoices** for past transactions.
- 🏠 Book **facilities** like clubhouses or theater halls.
- 🗣️ Raise and track **complaints**.
- 👨‍👩‍👧 Manage **family member profiles**.

### 🛠️ For Admin (Planned Enhancements):
- 📢 Post image-based announcements.
- 📋 Manage all bills and payments.
- 👨‍🔧 Add and monitor vendors.
- 📈 Access analytics and payment dashboards.

---

## 💡 Future Enhancements

- 🔐 Admin & User **role-based login**.
- 📊 Graphical **expense summaries**.
- 📦 **Visitor & parcel tracking**.
- 🔔 **Push notifications**.
- 🌍 Web dashboard for admin.

---

## 🧪 Software Requirements

| Component      | Version / Tool          |
|----------------|-------------------------|
| Language       | Java                    |
| IDE            | Android Studio (2021+)  |
| SDK            | Android SDK 30+         |
| Database       | Firebase Realtime DB    |
| Auth           | Firebase Authentication |
| Image Upload   | Cloudinary (optional)   |
| Payment API    | Razorpay / UPI          |

---

## 💻 Hardware Requirements

- Laptop or PC with:
  - 💾 **4 GB RAM** (8 GB Recommended)
  - 💽 **i3+ Processor**
  - 🗃️ **5 GB Disk Space**
- 📱 Android device or emulator

---

## 🧾 Bill Flow Overview

1. 🔍 Fetch dynamic bills from Firebase.
2. 💰 Initiate payment using Razorpay.
3. ✅ On success:
   - Save payment under `/payments/`.
   - Update `/users/{userId}/bills/{billId}/status` → `"paid"`.
   - Record timestamp & transaction ID.
4. 🧾 Option to download invoice as PDF.

---
## 🧑‍💻 Developer

- 👨‍💻 **Avinash Parmar**  
  📧 *avinashparmar787@gmail.com*  
  🌐 [LinkedIn](www.linkedin.com/in/avinashparmar117) | [GitHub](https://github.com/AvinashParmar)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 🙌 Support & Contributions

If you find this project useful, please ⭐ **star** the repo, fork it, or contribute with pull requests.
