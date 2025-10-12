# task_planner - Backend aplikacji do zarządzania zadaniami

Cel aplikacji:
Aplikacja umożliwia użytkownikom tworzenie i zarządzanie własnymi listami zadań do wykonania.

Zadania:
Każde zadanie posiada następujące atrybuty:

- ID
- Tytuł
- Data
- Czas rozpoczęcia
- Czas zakończenia
- Opis (opcjonalny)
- Użytkownik, do którego przypisano zadanie

Użytkownicy:
Każdy użytkownik ma następujące atrybuty:

- ID
- Nazwa użytkownika
- Email
- Hasło
- Rola (BASIC lub ADMIN)
- Status konta (informacja, czy konto zostało zablokowane)
- Lista zadań przypisana do użytkownika

Uprawnienia użytkowników
1. Podstawowy użytkownik (BASIC)

Podstawowy użytkownik może:

- Zarejestrować się w aplikacji (/register)
- Zalogować się do aplikacji (/login)
- Przeglądać swój profil (/myprofile)
- Usunąć swoje konto (/deleteaccount)
- Wyświetlić listę swoich zadań (/mytasks)
- Tworzyć nowe zadania (/mytasks/create)
- Edytować istniejące zadania (/mytasks/edit/{id})
- Usuwać zadania (/mytasks/delete/{id})

2. Administrator (ADMIN)

Administrator posiada rozszerzone uprawnienia do zarządzania użytkownikami i ich zadaniami.
Domyślnym kontem administratora jest admin@gmail.com. Podając ten adres podczas rejestracji, konto automatycznie otrzymuje uprawnienia ADMIN.

Administrator może:

- Zalogować się do panelu administratora (/admin/login)
- Przeglądać listę wszystkich użytkowników (/admin/dashboard)
- Wyświetlać szczegóły i zadania wybranego użytkownika (/admin/users/view/{id})
- Modyfikować parametry użytkownika, takie jak rola i blokada konta (/admin/users/advanced/{id})
- Usuwać konto wybranego użytkownika (/admin/users/{userId}/tasks/{taskId})
- Przeglądać i edytować zadania użytkownika (/admin/users/{userId}/tasks/edit/{taskId})
- Usuwać zadania użytkownika (/admin/login)
