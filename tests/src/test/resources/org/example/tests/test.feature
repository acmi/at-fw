Feature: Test
  Scenario: 123
    Given открыта страница https://ya.ru/
    Given модель страницы Ya
    Then проверить, что отображается элемент textInput
    When текст элемента textInput равен 'тест'
    And осуществлен клик по элементу searchButton
    Then проверить, что осуществлен переход на страницу /search/