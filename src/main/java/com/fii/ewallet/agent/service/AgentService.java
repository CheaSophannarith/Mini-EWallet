package com.fii.ewallet.agent.service;

import com.fii.ewallet.agent.dto.AddMoneyToUserRequest;
import com.fii.ewallet.agent.dto.AgentDashboardSeriesResponse;
import com.fii.ewallet.agent.dto.AgentDashboardSummaryResponse;
import com.fii.ewallet.agent.dto.TransactionListResponse;
import org.springframework.data.domain.Page;

public interface AgentService {

    void AddMoneyToUserWallet(String email, AddMoneyToUserRequest addMoneyToUserRequest);

    Page<TransactionListResponse> getTransactions(String email, int page, int size);

    AgentDashboardSummaryResponse getDashboardSummary(String email, String range);

    AgentDashboardSeriesResponse getDepositsSeries(String email, String range, String bucket);

}
