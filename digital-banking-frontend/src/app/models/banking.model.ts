export interface Customer {
  id?: number;
  name: string;
  email: string;
}

export type AccountStatus = 'CREATED' | 'ACTIVATED' | 'SUSPENDED';
export type OperationType = 'CREDIT' | 'DEBIT';
export type AccountType = 'CurrentAccount' | 'SavingAccount';

export interface BankAccount {
  id: string;
  balance: number;
  createdAt: string;
  status: AccountStatus;
  currency: string;
  customerDTO: Customer;
  type: AccountType;
  overDraft?: number;      // CurrentAccount
  interestRate?: number;   // SavingAccount
}

export interface AccountOperation {
  id: number;
  operationDate: string;
  amount: number;
  type: OperationType;
  description: string;
}

export interface AccountHistory {
  accountId: string;
  balance: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDTOS: AccountOperation[];
}

export interface CurrentAccountRequest {
  initialBalance: number;
  overDraft: number;
  currency: string;
  customerId: number;
}

export interface SavingAccountRequest {
  initialBalance: number;
  interestRate: number;
  currency: string;
  customerId: number;
}

export interface DebitRequest {
  accountId: string;
  amount: number;
  description: string;
}

export interface CreditRequest {
  accountId: string;
  amount: number;
  description: string;
}

export interface TransferRequest {
  accountSource: string;
  accountDestination: string;
  amount: number;
  description: string;
}

export interface TopAccount {
  accountId: string;
  customerName: string;
  type: AccountType;
  balance: number;
  currency: string;
}

export interface DashboardStats {
  totalCustomers: number;
  totalAccounts: number;
  totalOperations: number;
  totalBalance: number;
  accountsByType: Record<string, number>;
  balanceByType: Record<string, number>;
  operationsByType: Record<string, number>;
  amountByOperationType: Record<string, number>;
  topAccounts: TopAccount[];
}
