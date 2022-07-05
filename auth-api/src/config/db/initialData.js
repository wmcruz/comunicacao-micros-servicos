import bcrypt from "bcrypt";

import User from "../../modules/user/model/User.js";

export async function createInitialData() {
    try {
        await User.sync({ force: true });
    
        let password = await bcrypt.hash("123456", 10);
    
        await User.create({
            name: "User Test 1",
            email: "testuser1@gmail.com",
            password: password
        });

        await User.create({
            name: "User Test 2",
            email: "testuser2@gmail.com",
            password: password
        });
    } catch (error) {
        console.error("Error initialData: " + error.message);
    }
}
