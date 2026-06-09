import { baseApi } from '@/store/api/baseApi';
import {
  AllocationMasterListResponse,
  CreateSessionApiResponse,
  CreateSessionRequest,
} from '../type/investment-amount.type';

const INVESTMENT_TYPE_OPTIONS_URL = '/api/v1/allocation-master';
const CREATE_SESSION_URL = '/api/v1/investment/session';

export const investmentAmountApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInvestmentTypeOptions: builder.query<AllocationMasterListResponse, void>({
      query: () => ({
        url: INVESTMENT_TYPE_OPTIONS_URL,
        method: 'GET',
      }),
    }),
    createInvestmentSession: builder.mutation<CreateSessionApiResponse, CreateSessionRequest>({
      query: (body) => ({
        url: CREATE_SESSION_URL,
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const {
  useGetInvestmentTypeOptionsQuery,
  useCreateInvestmentSessionMutation,
} = investmentAmountApi;