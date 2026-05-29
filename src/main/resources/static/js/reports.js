$.getJSON('/api/reports', function(reports) {
    if (reports.length === 0) {
        $('#report-list').html('<p class="text-center">日報がありません。</p>');
        return;
    }

    let html = '<div class="report-list">';
    reports.forEach(function(report) {
        html += '<div class="report-item">';
        html += '<h3 class="report-title">' + report.title + '</h3>';
        html += '<div class="report-meta">';
        html += '<span>' + report.submissionDate + '</span>';
        html += '<span class="report-author">作成者: ' + report.user.name + '</span>';
        html += '</div>';
        html += '<div class="button-group">';
        html += '<a href="/reports/' + report.id + '" class="btn btn-primary">詳細</a>';
        html += '</div>';
        html += '</div>';
    });
    html += '</div>';

    $('#report-list').html(html);
});
