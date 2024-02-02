Acceptance Criteria

1. Users can create only one Study Group for a single Subject.
2. The name of a study group:
   o Should have size between 5-30 characters.
   o Have alphanumeric format.
   o Special characters can only be – or _ (dash or underscore) and no other special characters should be allowed.
3. The only valid Subjects are:
   o Math
   o Chemistry
   o Physics
4. The creation time of a Study Group should be recorded.
5. Users can join a Study Group for different Subjects.
6. Users can leave the Study Groups they joined.
7. Users can view the list of all existing Study Groups:
   o Users can filter Study Groups by a given Subject.
   o Users can sort to see most recently created Study Groups or oldest ones.
8. Delete a Study Group if user is an owner.

Test Task
We need you to do the following as part of this task:

1. Write a list of different test cases to check this feature and the integrity of new entity StudyGroups according to
   the acceptance criteria:
   a. Describe some high-level steps and expectations (you can make assumptions of how the app works - just explain it)
   b. Highlight what are the inputs you will be using on each test case
   c. Define testing level of this test case: unit testing, component testing or e2e testing (manual) - considering
   that:
   i. We have a unit test framework in TestApp using Junit framework
   ii. We have a component test framework in our TestAppAPI using Junit framework
   iii. We do not have any automation to test the UI, so manual testing will be required on e2e level
   d. Consider if you want to add all test cases to regression or not ??
2. Write the code for all automated tests you described on the different frameworks
3. Write a SQL query that will return "all the StudyGroups which have at least a user with 'name' starting on 'R' sorted
   by 'creation date'" like "Roman" or "Rachel".
4. Organize the outcome of your work best for readers and reviewers - use MS Word/Excel.
5. Alternatively, you can create a github repository in epam github and share the repo.
